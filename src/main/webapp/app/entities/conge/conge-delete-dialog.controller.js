(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('CongeDeleteController',CongeDeleteController);

    CongeDeleteController.$inject = ['$uibModalInstance', 'entity', 'Conge'];

    function CongeDeleteController($uibModalInstance, entity, Conge) {
        var vm = this;

        vm.conge = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Conge.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
