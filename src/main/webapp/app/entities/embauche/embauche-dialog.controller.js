(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('EmbaucheDialogController', EmbaucheDialogController);

    EmbaucheDialogController.$inject = ['$timeout', '$scope', '$stateParams', '$uibModalInstance', '$q', 'entity', 'Embauche', 'User'];

    function EmbaucheDialogController ($timeout, $scope, $stateParams, $uibModalInstance, $q, entity, Embauche, User) {
        var vm = this;

        vm.embauche = entity;
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
            if (vm.embauche.id !== null) {
                Embauche.update(vm.embauche, onSaveSuccess, onSaveError);
            } else {
                Embauche.save(vm.embauche, onSaveSuccess, onSaveError);
            }
        }

        function onSaveSuccess (result) {
            $scope.$emit('iodevApp:embaucheUpdate', result);
            $uibModalInstance.close(result);
            vm.isSaving = false;
        }

        function onSaveError () {
            vm.isSaving = false;
        }

        vm.datePickerOpenStatus.dateEmbauche = false;

        function openCalendar (date) {
            vm.datePickerOpenStatus[date] = true;
        }
    }
})();
