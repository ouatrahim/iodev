'use strict';

describe('Controller Tests', function() {

    describe('Disponibilite Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockDisponibilite, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockDisponibilite = jasmine.createSpy('MockDisponibilite');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Disponibilite': MockDisponibilite,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("DisponibiliteDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'iodevApp:disponibiliteUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
