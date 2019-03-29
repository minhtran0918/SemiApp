class LatLng {
    constructor(lat, lng) {
        this._latitude = lat;    //field
        this._longitude = lng;    //field
    }
    get latitude() {   //getter
        return this._latitude;
    }

    set latitude(value) { //setter
        this._latitude = value;
    }

    get longitude() { //getter
        return this._longitude;
    }

    set longitude(value) { //setter
        this._longitude = value;
    }
}

class EarthQuadGrid {
    constructor() {
        if(EarthQuadGrid.instance) {
            return EarthQuadGrid.instance;
        }
    }
    searchPath(point, desiredLevel) {
        if(desiredLevel > EarthQuadGrid.MAX_LEVELS || desiredLevel < 0) {
            return -1;
        }
        const pLat = point.latitude;
        const pLng = point.longitude;
        let lat1 = EarthQuadGrid.TOP_LEFT.latitude, lng1 = EarthQuadGrid.TOP_LEFT.longitude;
        let lat2 = EarthQuadGrid.BOTTOM_RIGHT.latitude, lng2 = EarthQuadGrid.BOTTOM_RIGHT.longitude;
        let positionCode = 0;
        for(let level = 0; level <= desiredLevel; level++) {
            const midLng = (lng1 + lng2) / 2;
            const midLat = (lat1 + lat2) / 2;
            if(pLng >= lng1 && pLng <= midLng) {
                if(pLat <= lat1 && pLat >= midLat) {
                    lng2 = midLng; lat2 = midLat;
                    continue;
                } else if(pLat >= lat2 && pLat <= midLat) {
                    const tmp = Math.pow(4, (EarthQuadGrid.MAX_LEVELS - level));
                    positionCode += 1 * tmp;
                    lat1 = midLat;
                    lng2 = midLng;
                    continue;
                }
            } else if(pLng >= midLng && pLng <= lng2) {
                if(pLat <= lat1 && pLat >= midLat) {
                    const tmp = Math.pow(4, (EarthQuadGrid.MAX_LEVELS - level));
                    positionCode += 3 * tmp;
                    lng1 = midLng;
                    lat2 = midLat;
                    continue;
                } else if(pLat >= lat2 && pLat <= midLat) {
                    const tmp = Math.pow(4, (EarthQuadGrid.MAX_LEVELS - level));
                    positionCode += 2 * tmp;
                    lng1 = midLng; lat1 = midLat;
                    continue;
                }
            }
            return -1;
        }
        return positionCode;
    }

    getCollisionLevel(dimen) {
        const tmpLevel = Math.floor(Math.log2(EarthQuadGrid.DIMEN / dimen) - 1);
        return (tmpLevel > EarthQuadGrid.MAX_LEVELS)?EarthQuadGrid.MAX_LEVELS : tmpLevel; 
    }

    searchCollisionBoxes(center, dimen) {

        const level = this.getCollisionLevel(dimen); 
        const halfDimen = dimen / 2;
        const pointSet = new Set();
        //anti-clockwise
        pointSet.add(this.searchPath(new LatLng(center.latitude + halfDimen, center.longitude - halfDimen),
                level))
        .add(this.searchPath(new LatLng(center.latitude - halfDimen, center.longitude - halfDimen),
                level))
        .add(this.searchPath(new LatLng(center.latitude - halfDimen, center.longitude + halfDimen),
                level))
        .add(this.searchPath(new LatLng(center.latitude + halfDimen, center.longitude + halfDimen),
                level));
        return pointSet;
    } 
}
EarthQuadGrid.instance = new EarthQuadGrid();
EarthQuadGrid.MAX_LEVELS = 17;
EarthQuadGrid.TOP_LEFT = new LatLng(180, -180);
EarthQuadGrid.BOTTOM_RIGHT = new LatLng(-180, 180);
EarthQuadGrid.DIMEN = EarthQuadGrid.TOP_LEFT.latitude - EarthQuadGrid.BOTTOM_RIGHT.latitude;

function kmToLat(km) {
    return km / 111.111;
}

function getPath(pointStr, level) {
    const arr = pointStr.split(',');
    const point = new LatLng(parseFloat(arr[0]), parseFloat(arr[1]));
    let dLevel = EarthQuadGrid.MAX_LEVELS;
    if(level !== "" && level !== null) {  
        dLevel = parseInt(level);
    }
    const path = EarthQuadGrid.instance.searchPath(point, dLevel);
    document.getElementById("path").innerHTML="Path: " + path;
}

function getPoints(pointStr, dimen) {
    const arr = pointStr.split(',');
    const point = new LatLng(parseFloat(arr[0]), parseFloat(arr[1]));
    const dDimen = kmToLat(parseFloat(dimen));
    const set = EarthQuadGrid.instance.searchCollisionBoxes(point, dDimen);
    let res = EarthQuadGrid.instance.getCollisionLevel(dDimen) + '<br />';
    for(let e of set) {
        res += e + '<br />'
    }
    document.getElementById("points").innerHTML = res;
}