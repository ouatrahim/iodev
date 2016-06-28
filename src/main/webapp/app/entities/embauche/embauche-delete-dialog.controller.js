(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('EmbaucheDeleteController',EmbaucheDeleteController);

    EmbaucheDeleteController.$inject = ['$uibModalInstance', 'entity', 'Embauche'];

    function EmbaucheDeleteController($uibModalInstance, entity, Embauche) {
        var vm = this;

        vm.embauche = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Embauche.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
