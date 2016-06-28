(function() {
    'use strict';

    angular
        .module('iodevApp')
        .controller('HomeController', HomeController);

    HomeController.$inject = ['$scope', 'Principal', 'LoginService', '$state','Conge','CongeSearch','Salaire','Disponibilite'];

    function HomeController ($scope, Principal, LoginService, $state, Conge, CongeSearch, Salaire, Disponibilite) {
        var vm = this;

        vm.account = null;
        vm.isAuthenticated = null;
        vm.login = LoginService.open;
        vm.register = register;
        vm.conges = [];
        vm.salaires = [];
        vm.disponibilites = [];
        vm.search = search;
        $scope.$on('authenticationSuccess', function() {
            getAccount();
        });
        getAccount();
        loadAll();
        loadSAll();
        loadDAll();
        

        function getAccount() {
            Principal.identity().then(function(account) {
                vm.account = account;
                vm.isAuthenticated = Principal.isAuthenticated;
            });
        }
        
        function loadAll() {
            Conge.query(function(result) {
                vm.conges = result;
            });
        }
        
        function loadSAll() {
            Salaire.query(function(result) {
                vm.salaires = result;
            });
        }
        
        function loadDAll() {
            Disponibilite.query(function(result) {
                vm.disponibilites = result;
            });
        }
        
        function search () {
            if (!vm.searchQuery) {
                return vm.loadAll();
            }
            CongeSearch.query({query: vm.searchQuery}, function(result) {
                vm.conges = result;
            });
        }
        
        
        function register () {
            $state.go('register');
        }
    }
})();
