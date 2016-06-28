(function() {
    'use strict';
    angular
        .module('iodevApp')
        .factory('Disponibilite', Disponibilite);

    Disponibilite.$inject = ['$resource', 'DateUtils'];

    function Disponibilite ($resource, DateUtils) {
        var resourceUrl =  'api/disponibilites/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.lastUpdate = DateUtils.convertLocalDateFromServer(data.lastUpdate);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.lastUpdate = DateUtils.convertLocalDateToServer(data.lastUpdate);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.lastUpdate = DateUtils.convertLocalDateToServer(data.lastUpdate);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
