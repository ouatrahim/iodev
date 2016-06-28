(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('CongeController', CongeController);

    CongeController.$inject = ['$scope', '$state', 'Conge', 'CongeSearch'];

    function CongeController ($scope, $state, Conge, CongeSearch) {
        var vm = this;
        
        vm.conges = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Conge.query(function(result) {
                vm.conges = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CongeSearch.query({query: vm.searchQuery}, function(result) {
                vm.conges = result;
            });
        }    }
})();
