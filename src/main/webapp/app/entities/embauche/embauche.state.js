(function() {
    'use strict';

    angular
        .module('iodevApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('embauche', {
            parent: 'entity',
            url: '/embauche',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.embauche.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/embauche/embauches.html',
                    controller: 'EmbaucheController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('embauche');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('embauche-detail', {
            parent: 'entity',
            url: '/embauche/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.embauche.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/embauche/embauche-detail.html',
                    controller: 'EmbaucheDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('embauche');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Embauche', function($stateParams, Embauche) {
                    return Embauche.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('embauche.new', {
            parent: 'embauche',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/embauche/embauche-dialog.html',
                    controller: 'EmbaucheDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                dateEmbauche: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('embauche', null, { reload: true });
                }, function() {
                    $state.go('embauche');
                });
            }]
        })
        .state('embauche.edit', {
            parent: 'embauche',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/embauche/embauche-dialog.html',
                    controller: 'EmbaucheDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Embauche', function(Embauche) {
                            return Embauche.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('embauche', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('embauche.delete', {
            parent: 'embauche',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/embauche/embauche-delete-dialog.html',
                    controller: 'EmbaucheDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Embauche', function(Embauche) {
                            return Embauche.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('embauche', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
