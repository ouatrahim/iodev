(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('DisponibiliteDetailController', DisponibiliteDetailController);

    DisponibiliteDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Disponibilite', 'User'];

    function DisponibiliteDetailController($scope, $rootScope, $stateParams, entity, Disponibilite, User) {
        var vm = this;

        vm.disponibilite = entity;

        var unsubscribe = $rootScope.$on('iodevApp:disponibiliteUpdate', function(event, result) {
            vm.disponibilite = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
