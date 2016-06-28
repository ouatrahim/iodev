(function() {
    'use strict';

    angular
        .module('iodevApp')
        .factory('TestSearch', TestSearch);

    TestSearch.$inject = ['$resource'];

    function TestSearch($resource) {
        var resourceUrl =  'api/_search/tests/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
