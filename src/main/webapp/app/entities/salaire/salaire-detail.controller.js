(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('SalaireDetailController', SalaireDetailController);

    SalaireDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Salaire', 'User'];

    function SalaireDetailController($scope, $rootScope, $stateParams, entity, Salaire, User) {
        var vm = this;

        vm.salaire = entity;

        var unsubscribe = $rootScope.$on('iodevApp:salaireUpdate', function(event, result) {
            vm.salaire = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
