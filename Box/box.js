function compute(a, b, c, d, e) {
			let numsA = getNumbers(a);
			let numsB = getNumbers(b);
			let numsC = getNumbers(c);
			let numsD = getNumbers(d);
			let xArr = [numsA[1], numsB[1], numsC[1], numsD[1]];
			let yArr = [numsA[0], numsB[0], numsC[0], numsD[0]];
			let maxX = max(xArr); 
			let minX = min(xArr);
			let maxY = max(yArr);
			let minY = min(yArr);
			if(e) {
				let dx = maxX - minX;
				let dy = maxY - minY;
				if(dx > dy) {
					minY = maxY - dx;
				} else {
					maxX = minX + dy;
				}
			}
			document.getElementById("res").innerHTML="Top: "+maxY+', '+minX+"<br /> \
				Bottom: "+minY+', '+maxX;
		} 

		function max(arr) {
			if(arr.length <= 0) {
				return 0;
			}
			let tmp = arr[0];
			arr.forEach(e => {
				if(tmp < e) {
					tmp = e;
				}
			});
			return tmp;
		}

		function min(arr) {
			if(arr.length <= 0) {
				return 0;
			}
			let tmp = arr[0];
			arr.forEach(e => {
				if(tmp > e) {
					tmp = e;
				}
			});
			return tmp;
		}

		function getNumbers(str) {
			let arr = str.split(',');
			arr[0] = parseFloat(arr[0]);
			arr[1] = parseFloat(arr[1]);
			return arr;
		}