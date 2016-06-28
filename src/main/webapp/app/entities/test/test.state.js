(function() {
    'use strict';

    angular
        .module('iodevApp')
        .config(stateConfig);

    stateConfig.$inject = ['$stateProvider'];

    function stateConfig($stateProvider) {
        $stateProvider
        .state('test', {
            parent: 'entity',
            url: '/test',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.test.home.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/test/tests.html',
                    controller: 'TestController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('test');
                    $translatePartialLoader.addPart('global');
                    return $translate.refresh();
                }]
            }
        })
        .state('test-detail', {
            parent: 'entity',
            url: '/test/{id}',
            data: {
                authorities: ['ROLE_USER'],
                pageTitle: 'iodevApp.test.detail.title'
            },
            views: {
                'content@': {
                    templateUrl: 'app/entities/test/test-detail.html',
                    controller: 'TestDetailController',
                    controllerAs: 'vm'
                }
            },
            resolve: {
                translatePartialLoader: ['$translate', '$translatePartialLoader', function ($translate, $translatePartialLoader) {
                    $translatePartialLoader.addPart('test');
                    return $translate.refresh();
                }],
                entity: ['$stateParams', 'Test', function($stateParams, Test) {
                    return Test.get({id : $stateParams.id}).$promise;
                }]
            }
        })
        .state('test.new', {
            parent: 'test',
            url: '/new',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/test/test-dialog.html',
                    controller: 'TestDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: function () {
                            return {
                                testit: null,
                                id: null
                            };
                        }
                    }
                }).result.then(function() {
                    $state.go('test', null, { reload: true });
                }, function() {
                    $state.go('test');
                });
            }]
        })
        .state('test.edit', {
            parent: 'test',
            url: '/{id}/edit',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/test/test-dialog.html',
                    controller: 'TestDialogController',
                    controllerAs: 'vm',
                    backdrop: 'static',
                    size: 'lg',
                    resolve: {
                        entity: ['Test', function(Test) {
                            return Test.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('test', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        })
        .state('test.delete', {
            parent: 'test',
            url: '/{id}/delete',
            data: {
                authorities: ['ROLE_USER']
            },
            onEnter: ['$stateParams', '$state', '$uibModal', function($stateParams, $state, $uibModal) {
                $uibModal.open({
                    templateUrl: 'app/entities/test/test-delete-dialog.html',
                    controller: 'TestDeleteController',
                    controllerAs: 'vm',
                    size: 'md',
                    resolve: {
                        entity: ['Test', function(Test) {
                            return Test.get({id : $stateParams.id}).$promise;
                        }]
                    }
                }).result.then(function() {
                    $state.go('test', null, { reload: true });
                }, function() {
                    $state.go('^');
                });
            }]
        });
    }

})();
