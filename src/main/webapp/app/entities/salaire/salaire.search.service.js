(function() {
    'use strict';

    angular
        .module('iodevApp')
        .factory('SalaireSearch', SalaireSearch);

    SalaireSearch.$inject = ['$resource'];

    function SalaireSearch($resource) {
        var resourceUrl =  'api/_search/salaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
