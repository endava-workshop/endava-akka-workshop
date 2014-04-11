//<![CDATA[ 

angular.module('TabsApp', [])
.controller('TabsCtrl', ['$scope', function ($scope) {
    $scope.tabs = [{
            title: 'Crawl the Web',
            url: 'one.tpl.html'
        }, {
            title: 'Break Pass-protected Zip Files',
            url: 'two.tpl.html'
        }, {
            title: 'Additional Features',
            url: 'three.tpl.html'
    }];

    $scope.currentTab = 'one.tpl.html';

    $scope.onClickTab = function (tab) {
        $scope.currentTab = tab.url;
    }
    
    $scope.isActiveTab = function(tabUrl) {
        return tabUrl == $scope.currentTab;
    }
}]);
//]]>  