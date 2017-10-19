var app = angular.module('plunker', ['nvd3']);



app.controller('stockCtrl', function($scope, $http) {
    $scope.options = {
        chart: {
            type: 'multiChart',
            height: 200,
            width: 300,
            margin : {
                top: 20,
                right: 45,
                bottom: 45,
                left: 65
            },
            clipEdge: true,
            duration: 100,
            grouped: true,
            showControls: false,
            xAxis: {
//                axisLabel: 'Time (ms)',
                showMaxMin: true,
                tickFormat: function(d){
//                    return d3.format(',f')(d);
                    return d3.time.format('%x')(new Date(d))
                }
            },
            yAxis1: {
//                axisLabel: 'Y Axis',
                showMaxMin: true,
                tickFormat: function(d){
                    return d3.format(',f')(d);
                }
            },
            yAxis2: {
                showMaxMin: true,
                tickFormat: function(d){
                    return d3.format(',.2f')(d);
                }
            },
            showLegend: false,
            callback: function(chart){
                chart.bars1.stacked(true);
                chart.bars2.stacked(true);
                chart.update();            
              }
        }
    };
// Chart Data //////////////////////////////////////////////////////////////
	

//  	$scope.initChartData = function(stockId) {
//		var path = '/service/nvd3data/st?stockId='+stockId
//
//		$http.get(path).success(function(data) {
//            $scope.appChartData = [
//               convertToChartFormat(data, 'buy', 'bar', 1),
//               convertToChartFormat(data, 'sell', 'bar', 1),
//               convertToChartFormat(data, 'close', 'line', 2)
//            ];
//		    console.log($scope.appChartData);
//		});
//	};

    $scope.appChartTopBuyData =[];
	$scope.setChartTopBuyData = function() {
	    $scope.appChartTopBuyData.length = 0;
        var path = '/service/nvd3_chart/top?securityId='+$scope.securityId+'&type=b&days='+$scope.days   
        
        $http.post(path).success(function(data) {
            //console.log(data);
            
            for (var key in data) {
                var obj = [
                    convertToChartFormat(data[key], 'buy', 'bar', 1),
                    convertToChartFormat(data[key], 'sell', 'bar', 1),
                    convertToChartFormat(data[key], 'close', 'line', 2)
                ];
                var temp = {
                    key: key,
                    value: obj
                };
                $scope.appChartTopBuyData.push(temp);
            }

            //console.log($scope.appChartTopData);

        });
    };

//////////////////////////////////////////////////////////////////////

    $scope.appChartTopSellData =[];
	$scope.setChartTopSellData = function() {
	    $scope.appChartTopSellData.length = 0;
        var path = '/service/nvd3_chart/top?securityId='+$scope.securityId+'&type=s&days='+$scope.days  
        
        $http.post(path).success(function(data) {
            //console.log(data);
            
            for (var key in data) {
                var obj = [
                    convertToChartFormat(data[key], 'buy', 'bar', 1),
                    convertToChartFormat(data[key], 'sell', 'bar', 1),
                    convertToChartFormat(data[key], 'close', 'line', 2)
                ];
                var temp = {
                    key: key,
                    value: obj
                };
                $scope.appChartTopSellData.push(temp);
            }

            //console.log($scope.appChartTopData);

        });
    };

});

function convertToChartFormat(data, seriesName, type, yAxis){
	
	var returnValue;
	var convertedChartArray = [];
	var i;
	for (i = 0; i < data.length; i++) {
	    switch(seriesName){
	        case 'buy':
	            convertedChartArray.push( {x: data[i].tradeDateMinSec, y: data[i].buyStake});
                break;
            case 'sell':
                convertedChartArray.push( {x: data[i].tradeDateMinSec, y: data[i].sellStake});
                break;             
            case 'close':
                convertedChartArray.push( {x: data[i].tradeDateMinSec, y: data[i].close});
                break;
            
	    }
	}
	returnValue = {
        key: seriesName,
        type: type,
        yAxis: yAxis,
        values: convertedChartArray
    }
	return returnValue;
};