(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('EmbaucheController', EmbaucheController);

    EmbaucheController.$inject = ['$scope', '$state', 'Embauche', 'EmbaucheSearch'];

    function EmbaucheController ($scope, $state, Embauche, EmbaucheSearch) {
        var vm = this;
        
        vm.embauches = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Embauche.query(function(result) {
                vm.embauches = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            EmbaucheSearch.query({query: vm.searchQuery}, function(result) {
                vm.embauches = result;
            });
        }    }
})();
