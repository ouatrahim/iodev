(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('DisponibiliteDialogController', DisponibiliteDialogController);

    DisponibiliteDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Disponibilite', 'User'];

    function DisponibiliteDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Disponibilite, User) {
        var vm = this;

        vm.disponibilite = entity;
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
            if (vm.disponibilite.id !== null) {
                Disponibilite.update(vm.disponibilite, onSaveSuccess, onSaveError);
            } else {
                Disponibilite.save(vm.disponibilite, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('iodevApp:disponibiliteUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.lastUpdate = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
