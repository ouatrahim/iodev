(function() {
    'use strict';

    angular
        .module('iodevApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('disponibilite', {
            parent: 'entity',
            url: '/disponibilite',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.disponibilite.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/disponibilite/disponibilites.html',
                    controller: 'DisponibiliteController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('disponibilite');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('disponibilite-detail', {
            parent: 'entity',
            url: '/disponibilite/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.disponibilite.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/disponibilite/disponibilite-detail.html',
                    controller: 'DisponibiliteDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('disponibilite');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Disponibilite', function($stateParams, Disponibilite) {
                    return Disponibilite.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('disponibilite.new', {
            parent: 'disponibilite',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/disponibilite/disponibilite-dialog.html',
                    controller: 'DisponibiliteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                lastUpdate: null,
                                congeDispo: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('disponibilite', null, { reload: true });
                }, function() {
                    $state.go('disponibilite');
                });
            }]
        })
        .state('disponibilite.edit', {
            parent: 'disponibilite',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/disponibilite/disponibilite-dialog.html',
                    controller: 'DisponibiliteDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Disponibilite', function(Disponibilite) {
                            return Disponibilite.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('disponibilite', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('disponibilite.delete', {
            parent: 'disponibilite',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/disponibilite/disponibilite-delete-dialog.html',
                    controller: 'DisponibiliteDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Disponibilite', function(Disponibilite) {
                            return Disponibilite.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('disponibilite', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
