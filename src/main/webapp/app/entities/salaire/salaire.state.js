(function() {
    'use strict';

    angular
        .module('iodevApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('salaire', {
            parent: 'entity',
            url: '/salaire',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.salaire.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/salaire/salaires.html',
                    controller: 'SalaireController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salaire');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('salaire-detail', {
            parent: 'entity',
            url: '/salaire/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.salaire.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/salaire/salaire-detail.html',
                    controller: 'SalaireDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('salaire');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Salaire', function($stateParams, Salaire) {
                    return Salaire.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('salaire.new', {
            parent: 'salaire',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/salaire/salaire-dialog.html',
                    controller: 'SalaireDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateAttribution: null,
                                montantSalaire: null,
                                montantSalaireActuel: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('salaire', null, { reload: true });
                }, function() {
                    $state.go('salaire');
                });
            }]
        })
        .state('salaire.edit', {
            parent: 'salaire',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/salaire/salaire-dialog.html',
                    controller: 'SalaireDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Salaire', function(Salaire) {
                            return Salaire.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('salaire', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('salaire.delete', {
            parent: 'salaire',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/salaire/salaire-delete-dialog.html',
                    controller: 'SalaireDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Salaire', function(Salaire) {
                            return Salaire.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('salaire', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
