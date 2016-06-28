'use strict';

describe('Controller Tests', function() {

    describe('Embauche Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockEmbauche, MockUser;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockEmbauche = jasmine.createSpy('MockEmbauche');
            MockUser = jasmine.createSpy('MockUser');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity ,
                'Embauche': MockEmbauche,
                'User': MockUser
            };
            createController = function() {
                $injector.get('$controller')("EmbaucheDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'iodevApp:embaucheUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
