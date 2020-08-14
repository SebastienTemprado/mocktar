import { Verbs } from './verbs.js';

var app = new Vue({
    el: '#app',
    data: { 
        verbs: Verbs,
        mocks: [],
        message: '',
        id: 0,
        apiName: '',
        name: '',
        verb: Verbs[0],
        request: '',
        queryParams: [],
        headerParams: [],
        body: '',
        response: '',
        formActivation: false,
        updating: false,
        errors: false,
        apiNameFieldError: false,
        nameFieldError: false,
        requestFieldError: false,
        responseFieldError: false
    },
    created: function() {
        const vm = this;
        vm.getMocks();
    },
    methods: {
        getMocks: function() {
            const vm = this;
            axios.get('http://localhost:8080/mocks')
            .then(function (response) {
                vm.mocks = response.data;
            })
            .catch(function (error) {
                vm.errors = true;
                vm.message = 'Error! Could not reach the API. ' + error;
            })
        },
        getMock: function(name) {
            const vm = this;
            this.$refs.addButton.textContent = '+ Add';
            axios.get(`http://localhost:8080/mocks?name=${name}`)
            .then(function (response) {
                if (response.data.length == 0) {
                    vm.message = 'No result';
                } else {
                    vm.message = response.data[0].id;
                    vm.id = response.data[0].id;
                    vm.apiName = response.data[0].apiName;
                    vm.name = response.data[0].name;
                    vm.verb = response.data[0].verb;
                    vm.request = response.data[0].request;
                    vm.queryParams = response.data[0].queryParams;
                    vm.headerParams = response.data[0].headerParams;
                    vm.body = response.data[0].body;
                    vm.response = response.data[0].response;
                    vm.formActivation = true;
                    vm.updating = true;
                    vm.errors = false;
                    vm.apiNameFieldError = false;
                    vm.nameFieldError = false;
                    vm.requestFieldError = false;
                    vm.responseFieldError = false;
                }
            })
            .catch(function (error) {
                vm.message = 'Error! Could not reach the API. ' + error;
                vm.errors = true;
                vm.formActivation = false;
            })
        },
        addMock: function() {
            const vm = this;
            vm.updating = false;
            if (this.$refs.addButton.textContent === "+ Add") {
                vm.clearForm();
                vm.formActivation = true;
                this.$refs.addButton.textContent = '\u2713 Add';
            } else {
                try {
                    this.validateForm(vm);
                    axios.post(`http://localhost:8080/mocks`, {
                        id: 0,
                        apiName: vm.apiName,
                        name: vm.name,
                        verb: vm.verb,
                        request: vm.request,
                        queryParams: vm.queryParams,
                        headerParams: vm.headerParams,
                        body: vm.body,
                        response: vm.response
                    })
                    .then(function (response) {
                        vm.message = `The mock ${vm.name} has been added!` ;
                        vm.getMocks();
                    })
                    .catch(function (error) {
                        vm.errors = true;
                        vm.message = `Error! Could not add the mock ${vm.name}.` + error;
                    })
                    this.$refs.addButton.textContent = '+ Add';
                    vm.formActivation = false;
                    vm.clearForm();
                } catch (error) {
                    vm.errors = true;
                    vm.message = error;
                }
            }
            
        },
        updateMock: function() {
            const vm = this;
            try {
                this.validateForm(vm);
                axios.put(`http://localhost:8080/mocks`, {
                    id: vm.id,
                    apiName: vm.apiName,
                    name: vm.name,
                    verb: vm.verb,
                    request: vm.request,
                    queryParams: vm.queryParams,
                    headerParams: vm.headerParams,
                    body: vm.body,
                    response: vm.response
                })
                .then(function (response) {
                    vm.message = `The mock ${vm.name} has been updated!` ;
                    vm.getMocks();
                })
                .catch(function (error) {
                    vm.errors = true;
                    vm.message = `Error! Could not add the mock ${vm.name}.` + error;
                })
                vm.formActivation = false;
                vm.updating = false;
                vm.clearForm();
            } catch (error) {
                vm.errors = true;
                vm.message = error;
            }
        },
        removeMock: function(name) {
            const vm = this;
            axios.delete(`http://localhost:8080/mocks/${name}`)
            .then(function (response) {
                vm.message = `mock ${name} deleted!` ;
                vm.getMocks();
                if (name == vm.name) {
                    vm.updating = false;
                    vm.formActivation = false;
                    vm.clearForm();
                }
            })
            .catch(function (error) {
                vm.errors = true;
                vm.message = `Error! Could not delete the mock ${name}.` + error;
            })
        },
        validateForm: function(vm) {
            vm.apiName === '' ? vm.apiNameFieldError = true : vm.apiNameFieldError = false;
            vm.name === '' ? vm.nameFieldError = true : vm.nameFieldError = false;
            vm.request === '' ? vm.requestFieldError = true : vm.requestFieldError = false;
            vm.response === '' ? vm.responseFieldError = true : vm.responseFieldError = false;
            
            if (vm.apiNameFieldError || vm.nameFieldError || vm.requestFieldError || vm.responseFieldError) {
                vm.errors = true;
                throw new Error(`Invalid form.`);
            } else {
                vm.errors = false;
            }

            if (vm.mocks.filter(m => m.name === vm.name && m.id !== vm.id).length > 0) {
                vm.errors = true;
                throw new Error('This mock already exists!');
            } 
            
        }, 
        clearForm: function() {
            const vm = this;
            vm.id = 0;
            vm.apiName = '';
            vm.name = '';
            vm.verb = Verbs[0];
            vm.request = '';
            vm.queryParams = [];
            vm.headerParams = [];
            vm.body = '';
            vm.response = '';
            vm.errors = false;
            vm.apiNameFieldError = false;
            vm.nameFieldError = false;
            vm.requestFieldError = false;
            vm.responseFieldError = false;
        },
        addQueryParam: function() {
            const vm = this;
            vm.queryParams.push({name: '', value: ''});
        },
        deleteQueryParam: function(indexQueryParamToDelete) {
            const vm = this;
            vm.queryParams.splice(indexQueryParamToDelete, 1);
        },
        addHeaderParam: function() {
            const vm = this;
            vm.headerParams.push({name: '', value: ''});
        },
        deleteHeaderParam: function(indexHeaderParamToDelete) {
            const vm = this;
            vm.headerParams.splice(indexHeaderParamToDelete, 1);
        }
    }
});