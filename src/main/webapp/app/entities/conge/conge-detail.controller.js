(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('CongeDetailController', CongeDetailController);

    CongeDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Conge', 'User'];

    function CongeDetailController($scope, $rootScope, $stateParams, entity, Conge, User) {
        var vm = this;

        vm.conge = entity;

        var unsubscribe = $rootScope.$on('iodevApp:congeUpdate', function(event, result) {
            vm.conge = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
