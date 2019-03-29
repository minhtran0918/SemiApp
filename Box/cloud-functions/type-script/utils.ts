import { LatLng } from "./LatLng";

export const RADIAN_PER_DEGREE = Math.PI / 180

export const kmToLat = function(km: number) {
    return km / 111.111;
}

export const kmToLng = function(km: number, lat: number) {
    return km / (111.111 * Math.cos(lat * RADIAN_PER_DEGREE));
}

export const strToNumericArray = function(str: string) {
    const strArr: string[] = str.split(',');
    const newArr: number[] = [];
    strArr.forEach(e => {
        newArr.push(parseInt(e));
    });
    return newArr;
}

export const strToLatLng = function(str: string) {
    const strArr: string[] = str.split(',');
    return new LatLng(parseFloat(strArr[0]), parseFloat(strArr[1]));
}

export const distance2 = function(location: LatLng, center: LatLng) {
    const dx: number = location.longitude - center.longitude;
    const dy: number = location.latitude - center.latitude;
    return dx * dx + dy * dy;
}

export const isObjectInRect = function(topLeft, bottomRight, location) {
    if(location.latitude <= topLeft.latitude &&
        location.latitude >= bottomRight.latitude &&
        location.longitude >= topLeft.longitude &&
        location.longitude <= bottomRight.longitude) {
        return true;
    }
    return false;
}

export const containsKeywords = function(keywordArray: string[], des: string[]) {
    if(keywordArray === null || keywordArray.length === 0) {
        return true;
    }
    if(keywordArray.length > des.length) {
        return false;
    }
    for(const keyword of keywordArray) {
        if(!exports.strBinSearch(des, keyword)) {
            return false;
        }
    }
    return true;
}

export const sortAsc = function(distance2s: number[], objects: Object[]) {
    const refObject = objects[objects.length - 1];
    const refDistance = distance2s[distance2s.length - 1];
    for(let index = objects.length - 2;
        index >= 0 && distance2s[index] > refDistance; index--) {
        objects[index + 1] = objects[index];
        objects[index] = refObject;
        distance2s[index + 1] = distance2s[index];
        distance2s[index] = refDistance;
    }
}

export const strBinSearch = function(arr: string[], str: string) {
    let left = 0;
    let right = arr.length - 1;
    if(str < arr[0] || str > arr[right]) {
        return false;
    } else if(str === arr[0] || str === arr[right]) {
        return true;
    }
    while(right - left !== 1) {
        const mid = Math.floor((left + right) / 2);
        const comResult = str.localeCompare(arr[mid]);
        if(comResult === 0) {
            return true;
        } else if(comResult === 1) {
            left = mid;
        } else {
            right = mid;
        }
    }
    return false;
}