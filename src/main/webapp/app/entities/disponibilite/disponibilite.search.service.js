(function() {
    'use strict';

    angular
        .module('iodevApp')
        .factory('DisponibiliteSearch', DisponibiliteSearch);

    DisponibiliteSearch.$inject = ['$resource'];

    function DisponibiliteSearch($resource) {
        var resourceUrl =  'api/_search/disponibilites/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
