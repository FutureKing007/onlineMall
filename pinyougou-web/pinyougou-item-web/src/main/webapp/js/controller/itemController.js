app.controller("itemController", function ($scope,$controller,$http) {

    $controller("baseController",{$scope:$scope});

        $scope.num = 1;

        $scope.addNum = function (n) {
            alert(JSON.stringify(itemList));
            $scope.num = parseInt($scope.num);
            $scope.num += n;
        };

        $scope.$watch("num", function (newVal, oldVal) {
            if (!/^\d+$/.test(newVal)) {
                $scope.num = oldVal;
            }
            if (newval < 1) {
                $scope.num = 1;
            }
        });

        //初始化用来记录顾客选择的
        $scope.specItems = {};

        $scope.selectSpec = function (attributeName, attributeValue) {
            $scope.specItems[attributeName] = attributeValue;
            searchSku();
        };

        $scope.isSelected = function (attributeName, attributeValue) {
            return $scope.specItems[attributeName] == attributeValue;
        };

        $scope.loadSku = function () {
            $scope.sku = itemList[0];
            $scope.specItems = JSON.parse($scope.sku.spec);
        };

    var searchSku = function () {
        itemList.forEach(function (item) {
            if (item.spec == JSON.stringify($scope.specItems)) {
                $scope.sku = item;
            }
        })
    };

    $scope.addToCart = function () {
        $http.get("http://cart.pinyougou.com/cart/addCart?itemId=" + $scope.sku.id + "&num=" + $scope.num,{"withCredentials":true})
            .then(function (response) {
                if(response.data) {
                    location.href = "http://cart.pinyougou.com/cart.html";
                }else {
                    alert("添加失败!")
                }
            });
    };

});
