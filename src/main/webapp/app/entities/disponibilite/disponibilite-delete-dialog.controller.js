(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('DisponibiliteDeleteController',DisponibiliteDeleteController);

    DisponibiliteDeleteController.$inject = ['$uibModalInstance', 'entity', 'Disponibilite'];

    function DisponibiliteDeleteController($uibModalInstance, entity, Disponibilite) {
        var vm = this;

        vm.disponibilite = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Disponibilite.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
