var app = new Vue({
    el: '#app',
    data: { 
        mocks: [],
        message: '',
        id: 0,
        name: '',
        request: '',
        response: '',
        formActivation: false,
        updating: false
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
                    vm.name = response.data[0].name;
                    vm.request = response.data[0].request;
                    vm.response = response.data[0].response;
                    vm.formActivation = true;
                    vm.updating = true;
                }
            })
            .catch(function (error) {
                vm.message = 'Error! Could not reach the API. ' + error;
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
                        name: vm.name,
                        request: vm.request,
                        response: vm.response
                    })
                    .then(function (response) {
                        vm.message = `mock ${vm.name} added!` ;
                        vm.getMocks();
                    })
                    .catch(function (error) {
                        vm.message = `Error! Could not add the mock ${vm.name}.` + error;
                    })
                    this.$refs.addButton.textContent = '+ Add';
                    vm.formActivation = false;
                    vm.clearForm();
                } catch (error) {
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
                    name: vm.name,
                    request: vm.request,
                    response: vm.response
                })
                .then(function (response) {
                    vm.message = `mock ${vm.name} updated!` ;
                    vm.getMocks();
                })
                .catch(function (error) {
                    vm.message = `Error! Could not add the mock ${vm.name}.` + error;
                })
                vm.formActivation = false;
                vm.updating = false;
                vm.clearForm();
            } catch (error) {
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
                vm.message = `Error! Could not delete the mock ${name}.` + error;
            })
        },
        validateForm: function(vm) {
            if (vm.name === '' || vm.request === '' || vm.response === '') {
                throw new Error(`Invalid form.`);
            }

            if (vm.mocks.filter(m => m.name === vm.name && m.id !== vm.id).length > 0) {
                throw new Error('This mock already exists!');
            } 
        }, 
        clearForm: function() {
            const vm = this;
            vm.id = 0;
            vm.name = '';
            vm.request = '';
            vm.response = '';
        }
    }
})