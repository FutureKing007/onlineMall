app.controller('vcodeController',function ($scope,$controller,baseService) {

    /** 指定继承baseController */
    $controller('baseController',{$scope:$scope});

    $scope.loadVCode = function () {
        $scope.vcodeURL = "/code?n=" + Math.random();
        baseService.sendGet($scope.vcodeURL)
    }
});