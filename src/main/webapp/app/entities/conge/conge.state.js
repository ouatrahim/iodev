(function() {
    'use strict';

    angular
        .module('iodevApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('conge', {
            parent: 'entity',
            url: '/conge',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.conge.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/conge/conges.html',
                    controller: 'CongeController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('conge');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('conge-detail', {
            parent: 'entity',
            url: '/conge/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.conge.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/conge/conge-detail.html',
                    controller: 'CongeDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('conge');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Conge', function($stateParams, Conge) {
                    return Conge.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('conge.new', {
            parent: 'conge',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-dialog.html',
                    controller: 'CongeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                libelle: null,
                                type: null,
                                dateDemande: null,
                                dateDebut: null,
                                dateFin: null,
                                valRH: false,
                                valDG: false,
                                derniereModification: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('home', null, { reload: true });
                }, function() {
                    $state.go('home');
                });
            }]
        })
        .state('conge.edit', {
            parent: 'conge',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-dialog.html',
                    controller: 'CongeDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Conge', function(Conge) {
                            return Conge.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('conge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('conge.delete', {
            parent: 'conge',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/conge/conge-delete-dialog.html',
                    controller: 'CongeDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Conge', function(Conge) {
                            return Conge.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('conge', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
