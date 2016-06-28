(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('TestDeleteController',TestDeleteController);

    TestDeleteController.$inject = ['$uibModalInstance', 'entity', 'Test'];

    function TestDeleteController($uibModalInstance, entity, Test) {
        var vm = this;

        vm.test = entity;
        vm.clear = clear;
        vm.confirmDelete = confirmDelete;
        
        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function confirmDelete (id) {
            Test.delete({id: id},
                function () {
                    $uibModalInstance.close(true);
                });
        }
    }
})();
