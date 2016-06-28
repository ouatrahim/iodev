(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('TestDialogController', TestDialogController);

    TestDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', 'entity', 'Test'];

    function TestDialogController ($timeout, $scope, $stateParams, $uibModalInstance, entity, Test) {
        var vm = this;

        vm.test = entity;
        vm.clear = clear;
        vm.save = save;

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.test.id !== null) {
                Test.update(vm.test, onSaveSuccess, onSaveError);
            } else {
                Test.save(vm.test, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('iodevApp:testUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }


    }
})();
