(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('TestDetailController', TestDetailController);

    TestDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Test'];

    function TestDetailController($scope, $rootScope, $stateParams, entity, Test) {
        var vm = this;

        vm.test = entity;

        var unsubscribe = $rootScope.$on('iodevApp:testUpdate', function(event, result) {
            vm.test = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
