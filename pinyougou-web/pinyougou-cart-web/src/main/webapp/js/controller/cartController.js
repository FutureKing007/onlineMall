/** 定义购物车控制器 */
app.controller('cartController', function($scope,$controller,baseService){
    // 指定继承baseController
    $controller('baseController', {$scope:$scope});


    /*获取购物车*/
    $scope.findCart = function () {
        baseService.sendGet("/cart/findCart")
            .then(function (response) {
                $scope.carts = response.data;

                $scope.totalEntity = {totalNum:0,totalMoney:0};
               for(var i = 0; i < response.data.length;i++) {
                   //拿到每个商家的cart
                   var cart = response.data[i];
                   for(var j=0;j<cart.orderItems.length;j++) {
                       //拿到商家的商品类表
                       var orderItem = cart.orderItems[j];
                       $scope.totalEntity.totalNum += orderItem.num;
                       $scope.totalEntity.totalMoney += orderItem.totalFee;
                   }
               }
            });
    };

    $scope.addCart = function(itemId,num) {
        baseService.sendGet("/cart/addCart","itemId="+itemId+"&num="+num)
            .then(function (response) {
            if(response.data) {
                $scope.findCart();
            }else {
                alert("加入购物车失败...");
            }
        });
    };

    $scope.inputNum = function (itemId,numInput) {
        for(var i =0;i<$scope.carts.length;i++) {
            var cart = $scope.carts[i];
            for(var j=0;j<cart.orderItems.length;j++) {
                //拿到商家的商品类表
                var orderItem = cart.orderItems[j];
                if(orderItem.itemId == itemId) {
                    $scope.addCart(itemId,numInput-orderItem.num);
                    orderItem.num = numInput;
                }
            }
        }
    };



});
