(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('EmbaucheDetailController', EmbaucheDetailController);

    EmbaucheDetailController.$inject = ['$scope', '$rootScope', '$stateParams', 'entity', 'Embauche', 'User'];

    function EmbaucheDetailController($scope, $rootScope, $stateParams, entity, Embauche, User) {
        var vm = this;

        vm.embauche = entity;

        var unsubscribe = $rootScope.$on('iodevApp:embaucheUpdate', function(event, result) {
            vm.embauche = result;
        });
        $scope.$on('$destroy', unsubscribe);
    }
})();
