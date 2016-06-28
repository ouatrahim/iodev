(function() {
    'use strict';
    angular
        .module('iodevApp')
        .factory('Embauche', Embauche);

    Embauche.$inject = ['$resource', 'DateUtils'];

    function Embauche ($resource, DateUtils) {
        var resourceUrl =  'api/embauches/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateEmbauche = DateUtils.convertLocalDateFromServer(data.dateEmbauche);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.dateEmbauche = DateUtils.convertLocalDateToServer(data.dateEmbauche);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.dateEmbauche = DateUtils.convertLocalDateToServer(data.dateEmbauche);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
