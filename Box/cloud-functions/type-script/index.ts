import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
import {C} from './C';
import {EarthQuadGrid} from './EarthQuadGrid';
import {LatLng} from './LatLng';
import * as utils from './utils';
admin.initializeApp();
// // Start writing Firebase Functions
// // https://firebase.google.com/docs/functions/typescript
//
// export const helloWorld = functions.https.onRequest((request, response) => {
//  response.send("Hello from Firebase!");
// });

/** input:
*data.from
*data.storeType
*data.centerLat
*data.centerLng
*data.storeType
*data.numResult
*data.selectedFields
*/
export const nearbyStores = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const selectedFields: string[] = data.selectedFields;
    const from: number = data.from;
    const type: number = data.storeType;
    let numResults: number = data.numResults;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const numSelectedFields: number = selectedFields.length;
    const latDimen: number = utils.kmToLat(C.NEARBY_DIMEN);
    const lngDimen: number = utils.kmToLng(C.NEARBY_DIMEN, center.latitude);
    const maxDimen: number = (latDimen >= lngDimen)?latDimen:lngDimen;
    const rects: Set<number> = grid.searchCollisionBoxes(center, maxDimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(maxDimen));
    const halfLatDimen: number = latDimen / 2;
    const halfLngDimen: number = lngDimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfLatDimen, center.longitude - halfLngDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfLatDimen, center.longitude + halfLngDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    const colRef: FirebaseFirestore.CollectionReference = admin.firestore().collection('store');
    let query: FirebaseFirestore.Query;
    if(selectedFields !== undefined) {
        selectedFields.push('geo');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let newQuery: FirebaseFirestore.Query = query.where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            newQuery = newQuery.where('type', '==', type);
        }
        promises.push(newQuery.get());
    }
    return Promise.all(promises).then(snapshots => {
        const stores: Object[] = [];
        const distance2s: number[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(store => {
                const storeData: FirebaseFirestore.DocumentData = store.data();
                const location: LatLng = new LatLng(storeData.geo.latitude, 
                    storeData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location)) {
                    const newData: Object = {};
                    newData['id'] =  store.id;
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField = selectedFields[i];
                        newData[selectedField] = storeData[selectedField];
                    }
                    distance2s.push(utils.distance2(location, center));
                    stores.push(newData);
                    utils.sortAsc(distance2s, stores);
                }
            });
        });
        if(numResults === undefined || numResults <= 0) {
            numResults = C.NUM_NEARBY_PRODUCTS;
        }
        return stores.slice(from, from + numResults);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

/** input:
*data.from
*data.centerLat
*data.centerLng
*data.storeType
*data.dimen
*data.keywords
*data.numResult
*data.selectedFields
*/
export const nearbyStoresByKeywords = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const selectedFields: string[] = data.selectedFields;
    const from: number = data.from;
    const type: number = data.storeType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const dimen: number = data.dimen;
    const keywords: string = data.keywords;
    let numResults: number = data.numResults;
    const latDimen: number = utils.kmToLat(dimen);
    const lngDimen: number = utils.kmToLng(dimen, center.latitude);
    const maxDimen: number = (latDimen >= lngDimen)?latDimen:lngDimen;
    const rects: Set<number> = grid.searchCollisionBoxes(center, maxDimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(maxDimen));
    const halfLatDimen: number = latDimen / 2;
    const halfLngDimen: number = lngDimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfLatDimen, center.longitude - halfLngDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfLatDimen, center.longitude + halfLngDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    const numSelectedFields = selectedFields.length;
    let keywordArray: string[] = null;
    let lastKeyword: string;
    const colRef: FirebaseFirestore.Query = admin.firestore().collection('store');
    let query: FirebaseFirestore.Query;
    if(selectedFields !== undefined) {
        selectedFields.push('geo', 'keywords');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }

    if(keywords !== undefined && keywords !== '') {
        keywordArray = keywords.split(/\s+/);
        lastKeyword = keywordArray.pop();
    } 
    //Query firestore
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let newQuery: FirebaseFirestore.Query = query.where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            newQuery = newQuery.where('type', '==', type);
        }
        if(keywords !== undefined && keywords !== '') {
            newQuery = newQuery.where('keywords', 'array-contains', lastKeyword);
        }
        promises.push(newQuery.get());
    }
    //proccess result
    return Promise.all(promises).then(snapshots => {
        const stores: Object[] = [];
        const distance2s: number[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(store => {
                const storeData: FirebaseFirestore.DocumentData = store.data();
                const location: LatLng = new LatLng(storeData.geo.latitude, 
                    storeData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location) && 
                    utils.containsKeywords(keywordArray, storeData.keywords)) {
                    const newData: Object = {};
                    newData['id'] =  store.id;
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField = selectedFields[i];
                        newData[selectedField] = storeData[selectedField];
                    }
                    distance2s.push(utils.distance2(location, center));
                    stores.push(newData);
                    utils.sortAsc(distance2s, stores);
                }
            });
        });
        if(numResults === undefined || numResults <= 0) {
            numResults = C.NUM_NEARBY_STORES_BY_KEYWORDS;
        }
        return stores.slice(from, from + numResults);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

/** input:
 * data.storeType
 * data.keywords
 * data.lastId
 * data.numResult
 * data.selectedFields
 * data.country
 * data.city
 * data.district
 * data.town
 */ 
export const storesByKeywords = functions.https.onCall(async (data, context)=> {
    const selectedFields: string[] = data.selectedFields;
    const keywordArray: string[] = data.keywords.split(/\s+/);
    const type: number = data.storeType;
    let lastId: string = data.lastId; 
    const country: number = data.country;
    const city: number = data.city;
    const district: number = data.district;
    const town: number = data.town;
    let numResults: number = data.numResults;
    const numSelectedFields: number = selectedFields.length;
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    const stores: Object[] = [];
    let docCount: number = 0;
    const colRef: FirebaseFirestore.CollectionReference = admin.firestore().collection('store');
    let query: FirebaseFirestore.Query;
    if(numResults === undefined || numResults <= 0) {
        numResults = C.NUM_STORES_BY_KEYWORDS;
    }
    if(selectedFields !== undefined) {
        selectedFields.push('keywords');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    if( lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    if(type !== undefined && type >= 0) {
        query = query.where('type', '==', type);
    }
    if(country >= 0) {
        query = query.where('address.country', '==', country);
    }
    if(city >= 0) {
        query = query.where('address.city', '==', city);
    }
    if(district >= 0) {
        query = query.where('address.district', '==', district);
    }
    if(town >= 0) {
        query = query.where('address.town', '==', town);
    }
    query = query.where('keywords', 'array-contains', keywordArray[keywordArray.length - 1]);
    keywordArray.pop();
    try {
        do {
            const promise = await query.orderBy(idField).startAfter(lastId)
                            .limit(C.NUM_STORES_PER_QUERY).get();
            const docs: FirebaseFirestore.QueryDocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const storeData: FirebaseFirestore.DocumentData = doc.data();
                if(utils.containsKeywords(keywordArray, storeData.keywords)) {
                    const newData: Object = {};
                    newData['id'] =  doc.id;
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField = selectedFields[i];
                        newData[selectedField] = storeData[selectedField];
                    }
                    stores.push(newData);
                    if(stores.length === numResults) {
                        return stores;
                    }
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return stores;
    } catch(err) {
        console.log(err);
        return [];
    }
});

/** input:
 * data.storeId : store id
 * data.lastId
 * data.selectedFields
 * data.numResult
 */
export const productsOfStore = functions.https.onCall(async (data, context) => {
    const selectedFields: string[] = data.selectedFields;
    const storeId: string = data.storeId;
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    let lastId: string = data.lastId;
    let numResults: number = data.numResults;
    let docCount: number = 0;
    const products: Object[] = [];
    const colRef: FirebaseFirestore.Query = admin.firestore().collection('product');
    let query: FirebaseFirestore.Query;
    if(numResults === undefined || numResults <= 0) {
        numResults = C.NUM_PRODUCTS_OF_STORE;
    }
    if(selectedFields !== undefined) {
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    if(lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    try {
        do {
            const promise = await query.where('storeId', '==', storeId).orderBy(idField)
                                .startAfter(lastId).limit(C.NUM_PRODUCTS_PER_QUERY)
                                .get();
            const docs: FirebaseFirestore.DocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const productData: FirebaseFirestore.DocumentData = doc.data();
                const newData: Object = {};
                newData['id'] = doc.id;
                for(const selectedField of selectedFields) {
                    newData[selectedField] = productData[selectedField];
                }
                products.push(newData);
                if(products.length === numResults) {
                    return products;
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return products;
    } catch(err) {
        console.log(err);
        return [];
    }
});


/** input:
 * data.storeId: store id
 */
export const storeById = functions.https.onCall((data, context) => {
    if(data.storeId === undefined || data.storeId === '') {
        return null;
    }
    return admin.firestore().doc('store/' + data.storeId).get()
    .then(snapshot => {
        const storeData: FirebaseFirestore.DocumentData = snapshot.data();
        storeData.id = snapshot.id;
        return storeData;
    })
    .catch(err => {
        console.log(err);
        return null
    });
});

/** input:
 * data.productId : product id
 */
export const productById = functions.https.onCall((data, context) => {
    if(data.productId === undefined || data.productId === '') {
        return null;
    }
    return admin.firestore().doc('product/' + data.productId).get()
    .then(snapshot => {
        const productData: FirebaseFirestore.DocumentData = snapshot.data();
        productData.id = snapshot.id;
        return productData;
    })
    .catch(err => {
        console.log(err);
        return null;
    });
});

/**
 * input:
 * data.productType
 * data.keywords
 * data.lastId 
 * data.country
 * data.city
 * data.district
 * data.town
 * data.selectedFields
 * data.numResult
 */ 
export const productsByKeywords = functions.https.onCall(async (data, context) => { 
    const keywordArray: string[] = data.keywords.split(/\s+/);
    const selectedFields: string[] = data.selectedFields;
    const type: number = data.productType;
    let lastId: string = data.lastId; 
    const country: number = data.country;
    const city: number = data.city;
    const district: number = data.district;
    const town: number = data.town;
    let numResults: number = data.numResults;
    const idField: FirebaseFirestore.FieldPath = admin.firestore.FieldPath.documentId();
    const products: Object[] = [];
    const numSelectedFields: number = selectedFields.length;
    let docCount: number = 0;
    const colRef: FirebaseFirestore.CollectionReference = admin.firestore().collection('product');
    let query: FirebaseFirestore.Query;
    if(numResults === undefined || numResults <= 0) {
        numResults = C.NUM_PRODUCTS_BY_KEYWORDS;
    }
    if(selectedFields !== undefined) {
        selectedFields.push('geo', 'keyword');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    if(lastId === undefined || lastId === '') {
        lastId = '\0';
    }
    if(type !== undefined && type >= 0) {
        query = query.where('type', '==', type);
    }
    if(country >= 0) {
        query = query.where('address.country', '==', country);
    }
    if(city >= 0) {
        query = query.where('address.city', '==', city);
    }
    if(district >= 0) {
        query = query.where('address.district', '==', district);
    }
    if(town >= 0) {
        query = query.where('address.town', '==', town);
    }
    query = query.where('keywords', 'array-contains', keywordArray[keywordArray.length - 1]);
    keywordArray.pop();
    try {
        do {
            const promise = await query.orderBy(idField).startAfter(lastId)
                            .limit(C.NUM_PRODUCTS_PER_QUERY).get();
            const docs: FirebaseFirestore.DocumentSnapshot[] = promise.docs;
            docCount = docs.length;
            for(const doc of docs) {
                const productData: FirebaseFirestore.DocumentData = doc.data();
                if(utils.containsKeywords(keywordArray, productData.keywords)) {
                    const newData: Object = {};
                    newData['id'] = doc.id;
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField = selectedFields[i];
                        newData[selectedField] = productData[selectedField];
                    }
                    products.push(newData);
                    if(products.length === numResults) {
                        return products;
                    }
                }
            }
            if(docCount > 0) {
                lastId = docs[docCount - 1].id;
            }
        } while(docCount > 0);
        return products;
    } catch(err) {
        console.log(err);
        return [];
    }
});

/**
 * input:
 * data.from
 * data.productType
 * data.numResult
 * data.selectedFields
 * data.centerLat
 * data.centerLng
 */
export const nearbyProducts = functions.https.onCall((data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const selectedFields: string[] = data.selectedFields;
    const from: number = data.from;
    const type: number = data.productType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    let numResults: number = data.numResults;
    const latDimen: number = utils.kmToLat(C.NEARBY_DIMEN);
    const lngDimen: number = utils.kmToLng(C.NEARBY_DIMEN, center.latitude);
    const maxDimen: number = (latDimen >= lngDimen)?latDimen:lngDimen;
    const rects: Set<number> = grid.searchCollisionBoxes(center, maxDimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(maxDimen));
    const halfLatDimen: number = latDimen / 2;
    const halfLngDimen: number = lngDimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfLatDimen, center.longitude - halfLngDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfLatDimen, center.longitude + halfLngDimen);
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    const numSelectedFields: number = selectedFields.length;
    const colRef: FirebaseFirestore.CollectionReference = admin.firestore().collection('product');
    let query: FirebaseFirestore.Query;
    if(selectedFields !== undefined) {
        selectedFields.push('geo');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let newQuery: FirebaseFirestore.Query = query.where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            newQuery = newQuery.where('type', '==', type);
        }
        promises.push(newQuery.get());
    }
    return Promise.all(promises).then(snapshots => {
        const products: Object[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(product => {
                const productData: FirebaseFirestore.DocumentData = product.data();
                const location: LatLng = new LatLng(productData.geo.latitude, 
                    productData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location)) {
                    const newData: Object = {};
                    newData['id'] = product.id;
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField: string = selectedFields[i];
                        newData[selectedField] = productData[selectedField];
                    }
                    products.push(newData);
                }
            });
        });
        if(numResults === undefined || numResults <= 0) {
            numResults = C.NUM_NEARBY_PRODUCTS;
        }
        return products.slice(from, from + numResults);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

/** input:
*data.from
*data.centerLat
*data.centerLng
*data.productType
*data.dimen
*data.keywords
*/
export const nearbyStoresByProducts = functions.https.onCall(async (data, context) => {
    const grid: EarthQuadGrid = EarthQuadGrid.instance;
    const selectedFields: string[] = data.selectedFields;
    const from: number = data.from;
    const type: number = data.productType;
    const center: LatLng = new LatLng(data.centerLat, data.centerLng);
    const dimen: number = data.dimen;
    const keywords: string = data.keywords;
    let numResults: number = data.numResults;
    const latDimen: number = utils.kmToLat(dimen);
    const lngDimen: number = utils.kmToLng(dimen, center.latitude);
    const maxDimen: number = (latDimen >= lngDimen)?latDimen:lngDimen;
    const rects: Set<number> = grid.searchCollisionBoxes(center, maxDimen);
    const limit: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - grid.getCollisionLevel(maxDimen));
    const halfLatDimen: number = latDimen / 2;
    const halfLngDimen: number = lngDimen / 2;
    const topLeft: LatLng = new LatLng(center.latitude + halfLatDimen, center.longitude - halfLngDimen);
    const bottomRight: LatLng = new LatLng(center.latitude - halfLatDimen, center.longitude + halfLngDimen);
    const productPromises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    const storePromises: Promise<FirebaseFirestore.DocumentSnapshot>[] = [];
    let keywordArray: string[] = null;
    let lastKeyword: string;
    if(numResults === undefined || numResults <= 0) {
        numResults = C.NUM_NEARBY_STORES_BY_PRODUCTS;
    }
    //Query firestore
    if(keywords !== undefined && keywords !== '') {
        keywordArray = keywords.split(/\s+/);
        lastKeyword = keywordArray.pop();
    } else {
        return getNearbyStores(rects, limit, from, topLeft, 
            bottomRight, center, selectedFields, numResults);
    }
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        let colRef: FirebaseFirestore.Query = admin.firestore().collection('product')
                        .select('geo', 'keywords', 'storeId')
                        .where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        if(type !== undefined && type >= 0) {
            colRef = colRef.where('type', '==', type);
        }
        if(keywords !== undefined && keywords !== '') {
            colRef = colRef.where('keywords', 'array-contains', lastKeyword);
        }
        productPromises.push(colRef.get());
    }
    try {
        const snapshots = await Promise.all(productPromises);
        const storeIdSet: Set<string> = new Set();
        snapshots.forEach(snapshot => {
            snapshot.forEach(product => {
                const productData: FirebaseFirestore.DocumentData = product.data();
                const location: LatLng = new LatLng(productData.geo.latitude, 
                    productData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location) && 
                    utils.containsKeywords(keywordArray, productData.keywords)) {
                        storeIdSet.add(productData.storeId);
                }
            });
        });
        for(const storeId of storeIdSet) {
            const storePromise = admin.firestore().doc('store/' + storeId).get();
            storePromises.push(storePromise);
        }
    } catch(err) {
        console.log(err);
        return [];
    }
    //
    return Promise.all(storePromises).then(snapshots => {
        const distance2s: number[] = [];
        const stores: Object[] = [];
        snapshots.forEach(snapshot => {
            const storeData: FirebaseFirestore.DocumentData = snapshot.data();
            const newData: Object = {};
            newData['id'] = snapshot.id;
            for(const selectedField of selectedFields) {
                newData[selectedField] = storeData[selectedField];
            }
            const location = new LatLng(storeData.geo.latitude, 
                storeData.geo.longitude);
            distance2s.push(utils.distance2(location, center));
            stores.push(newData);
            utils.sortAsc(distance2s, stores);
        });
        return stores.splice(from, from + numResults);
    }).catch(err => {
        console.log(err);
        return [];
    });
});

function getNearbyStores(rects: Set<number>, limit: number, from: number, 
    topLeft: LatLng, bottomRight: LatLng, center: LatLng, selectedFields: string[], numResults: number) {
    const numSelectedFields: number = selectedFields.length;
    const promises: Promise<FirebaseFirestore.QuerySnapshot>[] = [];
    const colRef: FirebaseFirestore.CollectionReference = admin.firestore().collection('store');
    let query: FirebaseFirestore.Query;
    if(selectedFields !== undefined) {
        selectedFields.push('geo');
        query = colRef.select.apply(colRef, selectedFields);
    } else {
        query = colRef;
    }
    for(const rect of rects) {
        const limitRect: number = rect + limit;
        const newQuery: FirebaseFirestore.Query = query.where('gridNumber', '>=', rect)
                        .where('gridNumber', '<', limitRect);
        promises.push(newQuery.get());
    }
    return Promise.all(promises).then(snapshots => {
        const stores: Object[] = [];
        const distance2s: number[] = [];
        snapshots.forEach(snapshot => {
            snapshot.forEach(store => {
                const storeData: FirebaseFirestore.DocumentData = store.data();
                const location: LatLng = new LatLng(storeData.geo.latitude, 
                    storeData.geo.longitude);
                if(utils.isObjectInRect(topLeft, bottomRight, location)) {
                    const newData: Object = {};
                    for(let i = 0; i < numSelectedFields; i++) {
                        const selectedField = selectedFields[i];
                        newData[selectedField] = storeData[selectedField];
                    }
                    distance2s.push(utils.distance2(location, center));
                    stores.push(newData);
                    utils.sortAsc(distance2s, stores);
                }
            });
        });
        return stores.slice(from, from + numResults);
    }).catch(err => {
        console.log(err);
        return [];
    });
}

export const getComments = functions.https.onCall(async (data, context) => {

    let numResults: number = data.numResults;
    const storeId: string = data.storeId;
    const colPath: string = 'store/' + storeId + '/comment';
    let promise: Promise<FirebaseFirestore.QuerySnapshot> ;
    if(numResults === undefined || numResults <= 0) {
        numResults = C.NUM_COMMENTS;
    }
    promise = admin.firestore().collection(colPath)
                    .orderBy('time', 'desc').limit(C.NUM_COMMENTS).get();
    return promise.then(snapshot => {
        const comments: Object[] = [];
        snapshot.forEach(async comment => {
            const commentData: FirebaseFirestore.DocumentData = comment.data();
            const user = await admin.auth().getUser(comment.id);
            commentData.id = comment.id;
            commentData.userDisplayName = user.displayName;
            commentData.userPhotoURL = user.photoURL;
            comments.push(commentData);
        });
        return comments;
    })
    .catch(err => {
        console.log(err);
        return [];
    });
})

export const postComment = functions.https.onCall(async (data, context) => {
    if(!context.auth) {
        return null;
    }
    const uid: string = context.auth.uid;
    const comment: string = data.comment;
    const storeId: string = data.storeId;
    const userRating: number = data.userRating;
    const time: FirebaseFirestore.Timestamp = admin.firestore.Timestamp.now();
    const storePath: string = 'store/' + storeId;
    const commentPath: string = storePath + '/comment/' + uid;
    let newCommentData: Object;
    const commentSnapshot: FirebaseFirestore.DocumentSnapshot = 
                            await admin.firestore().doc(commentPath).get();
    if(commentSnapshot.exists) {
        newCommentData = {
            'comment': comment,
            'userRating': userRating,
            'editTime': time,
        };
    } else {
        newCommentData = {
            'comment': comment,
            'userRating': userRating,
            'time': time,
            'editTime': time
        };
    }
    return admin.firestore().doc(commentPath).set(newCommentData).then(async () => {
        const docRef: FirebaseFirestore.DocumentReference = 
                            admin.firestore().doc(storePath);
        const docSnapshot: FirebaseFirestore.DocumentSnapshot = await docRef.get();
        const storeData: FirebaseFirestore.DocumentData = docSnapshot.data();
        const rating: number = storeData.rating;
        const numPoints: number = storeData.numPoints;
        const toltalPoints: number = rating * numPoints;
        let newRating: number;
        let newData: Object;
        if(!commentSnapshot.exists) {
            newRating = (toltalPoints + userRating) / (numPoints + 1);
            newData = {
                'numComments': storeData.numComments + 1,
                'numPoints': numPoints + 1,
                'rating': newRating
            };
        } else {
            const oldUserRating: number = commentSnapshot.data().userRating;
            newRating = (toltalPoints - oldUserRating + userRating) / numPoints;
            newData = {
                'rating': newRating
            };
        }
        await docRef.update(newData);
        return {'editTime': time};
    }).catch(err => {
        console.log(err);
        return {};
    });
});
