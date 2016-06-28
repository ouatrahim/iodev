(function() {
    'use strict';

    angular
        .module('iodevApp')
        .factory('EmbaucheSearch', EmbaucheSearch);

    EmbaucheSearch.$inject = ['$resource'];

    function EmbaucheSearch($resource) {
        var resourceUrl =  'api/_search/embauches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true}
        });
    }
})();
