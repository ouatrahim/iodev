(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('DisponibiliteController', DisponibiliteController);

    DisponibiliteController.$inject = ['$scope', '$state', 'Disponibilite', 'DisponibiliteSearch'];

    function DisponibiliteController ($scope, $state, Disponibilite, DisponibiliteSearch) {
        var vm = this;
        
        vm.disponibilites = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Disponibilite.query(function(result) {
                vm.disponibilites = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            DisponibiliteSearch.query({query: vm.searchQuery}, function(result) {
                vm.disponibilites = result;
            });
        }    }
})();
