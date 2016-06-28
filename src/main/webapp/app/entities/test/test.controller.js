(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('TestController', TestController);

    TestController.$inject = ['$scope', '$state', 'Test', 'TestSearch'];

    function TestController ($scope, $state, Test, TestSearch) {
        var vm = this;
        
        vm.tests = [];
        vm.search = search;

        loadAll();

        function loadAll() {
            Test.query(function(result) {
                vm.tests = result;
            });
        }

        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            TestSearch.query({query: vm.searchQuery}, function(result) {
                vm.tests = result;
            });
        }    }
})();
