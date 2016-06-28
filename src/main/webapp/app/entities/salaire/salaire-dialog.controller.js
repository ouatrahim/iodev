(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('SalaireDialogController', SalaireDialogController);

    SalaireDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Salaire', 'User'];

    function SalaireDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Salaire, User) {
        var vm = this;

        vm.salaire = entity;
        vm.clear = clear;
        vm.datePickerOpenStatus = {};
        vm.openCalendar = openCalendar;
        vm.save = save;
        vm.users = User.query();

        $timeout(function (){
            angular.element('.form-group:eq(1)>input').focus();
        });

        function clear () {
            $uibModalInstance.dismiss('cancel');
        }

        function save () {
            vm.isSaving = true;
            if (vm.salaire.id !== null) {
                Salaire.update(vm.salaire, onSaveSuccess, onSaveError);
            } else {
                Salaire.save(vm.salaire, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('iodevApp:salaireUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateAttribution = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
