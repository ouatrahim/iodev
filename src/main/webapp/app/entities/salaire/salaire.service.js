(function() {
    'use strict';
    angular
        .module('iodevApp')
        .factory('Salaire', Salaire);

    Salaire.$inject = ['$resource', 'DateUtils'];

    function Salaire ($resource, DateUtils) {
        var resourceUrl =  'api/salaires/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateAttribution = DateUtils.convertLocalDateFromServer(data.dateAttribution);
                    }
                    return data;
                }
            },
            'update': {
                method: 'PUT',
                transformRequest: function (data) {
                    data.dateAttribution = DateUtils.convertLocalDateToServer(data.dateAttribution);
                    return angular.toJson(data);
                }
            },
            'save': {
                method: 'POST',
                transformRequest: function (data) {
                    data.dateAttribution = DateUtils.convertLocalDateToServer(data.dateAttribution);
                    return angular.toJson(data);
                }
            }
        });
    }
})();
