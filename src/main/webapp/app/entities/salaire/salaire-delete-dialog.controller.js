(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('SalaireDeleteController',SalaireDeleteController);

    SalaireDeleteController.$inject = ['$uibModalInstance', 'entity', 'Salaire'];

    function SalaireDeleteController($uibModalInstance, entity, Salaire) {
        var vm = this;

        vm.salaire = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Salaire.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
