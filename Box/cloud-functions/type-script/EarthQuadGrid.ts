import {LatLng} from './LatLng';

export class EarthQuadGrid {
    public static readonly instance: EarthQuadGrid = new EarthQuadGrid();
    public static readonly MAX_LEVELS: number = 17;
    public static readonly TOP_LEFT: LatLng = new LatLng(180, -180);
    public static readonly BOTTOM_RIGHT: LatLng = new LatLng(-180, 180);
    public static readonly DIMEN: number = EarthQuadGrid.TOP_LEFT.latitude - EarthQuadGrid.BOTTOM_RIGHT.latitude;
    public static readonly MAX_GRID_NUMBER: number = 68719476735; //4^18:

    constructor() {
        return EarthQuadGrid.instance;
    }

    searchPath(point: LatLng, desiredLevel: number): number {
        if(desiredLevel > EarthQuadGrid.MAX_LEVELS || desiredLevel < 0) {
            return -1;
        }
        const pLat: number = point.latitude;
        const pLng: number = point.longitude;
        let lat1: number = EarthQuadGrid.TOP_LEFT.latitude;
        let lng1: number = EarthQuadGrid.TOP_LEFT.longitude;
        let lat2: number = EarthQuadGrid.BOTTOM_RIGHT.latitude;
        let lng2: number = EarthQuadGrid.BOTTOM_RIGHT.longitude;
        let positionCode: number = 0;
        for(let level = 0; level <= desiredLevel; level++) {
            const midLng: number = (lng1 + lng2) / 2;
            const midLat: number = (lat1 + lat2) / 2;
            if(pLng >= lng1 && pLng <= midLng) {
                if(pLat <= lat1 && pLat >= midLat) {
                    lng2 = midLng; lat2 = midLat;
                    continue;
                } else if(pLat >= lat2 && pLat <= midLat) {
                    const tmp: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - level);
                    positionCode += 1 * tmp;
                    lat1 = midLat;
                    lng2 = midLng;
                    continue;
                }
            } else if(pLng >= midLng && pLng <= lng2) {
                if(pLat <= lat1 && pLat >= midLat) {
                    const tmp: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - level);
                    positionCode += 3 * tmp;
                    lng1 = midLng;
                    lat2 = midLat;
                    continue;
                } else if(pLat >= lat2 && pLat <= midLat) {
                    const tmp: number = Math.pow(4, EarthQuadGrid.MAX_LEVELS - level);
                    positionCode += 2 * tmp;
                    lng1 = midLng; lat1 = midLat;
                    continue;
                }
            }
            return -1;
        }
        return positionCode;
    }

    public getCollisionLevel(dimen): number {
        const tmpLevel: number = Math.floor(Math.log2(EarthQuadGrid.DIMEN / dimen) - 1);
        return (tmpLevel > EarthQuadGrid.MAX_LEVELS) ? EarthQuadGrid.MAX_LEVELS : tmpLevel;
    }

    public searchCollisionBoxes(center: LatLng, dimen: number): Set<number> {
        const level: number = this.getCollisionLevel(dimen);
        const halfDimen: number = dimen / 2;
        const pointSet: Set<number> = new Set();
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
