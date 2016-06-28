(function() {
    'use strict';
    angular
        .module('iodevApp')
        .factory('Conge', Conge);

    Conge.$inject = ['$resource', 'DateUtils'];

    function Conge ($resource, DateUtils) {
        var resourceUrl =  'api/conges/:id';

        return $resource(resourceUrl, {}, {
            'query': { method: 'GET', isArray: true},
            'get': {
                method: 'GET',
                transformResponse: function (data) {
                    if (data) {
                        data = angular.fromJson(data);
                        data.dateDemande = DateUtils.convertDateTimeFromServer(data.dateDemande);
                        data.dateDebut = DateUtils.convertDateTimeFromServer(data.dateDebut);
                        data.dateFin = DateUtils.convertDateTimeFromServer(data.dateFin);
                        data.derniereModification = DateUtils.convertDateTimeFromServer(data.derniereModification);
                    }
                    return data;
                }
            },
            'update': { method:'PUT' }
        });
    }
})();
