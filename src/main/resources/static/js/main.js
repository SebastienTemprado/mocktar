function addMock() {
    alert("mock added!");
}

var app = new Vue({
    el: '#app',
    data: {
        mocks: [],
        message: '',
        name: '',
        request: '',
        response: ''
    },
    created: function() {
        const vm = this;
        axios.get('http://localhost:8080/mocks')
        .then(function (response) {
            vm.mocks = response.data;
        })
        .catch(function (error) {
            vm.message = 'Error! Could not reach the API. ' + error;
        })
    },
    methods: {
        getMock: function(name) {
            const vm = this;
            axios.get(`http://localhost:8080/mocks?name=${name}`)
            .then(function (response) {
                if (response.data.length == 0) {
                    vm.message = 'No result';
                } else {
                    vm.message = response.data[0].id;
                    vm.name = response.data[0].name;
                    vm.request = response.data[0].request;
                    vm.response = response.data[0].response;
                }
            })
            .catch(function (error) {
                vm.message = 'Error! Could not reach the API. ' + error;
            })
        },
        addMock: function() {
            const vm = this;
            if (this.$refs.addButton.textContent === "+") {
                vm.id = 0;
                vm.name = '';
                vm.request = '';
                vm.response = '';
                this.$refs.addButton.textContent = '\u2713';
            } else {
                this.$refs.addButton.textContent = '+';

                axios.post(`http://localhost:8080/mocks`, {
                    id: 0,
                    name: vm.name,
                    request: vm.request,
                    response: vm.response
                })
                .then(function (response) {
                    vm.message = `mock ${vm.name} added!` ;
                })
                .catch(function (error) {
                    vm.message = `Error! Could not add the mock ${vm.name}.` + error;
                })
            }
            
        },
        removeMock: function(name) {
            const vm = this;
            axios.delete(`http://localhost:8080/mocks/${name}`)
            .then(function (response) {
                vm.message = `mock ${name} deleted!` ;
            })
            .catch(function (error) {
                vm.message = `Error! Could not delete the mock ${name}.` + error;
            })
        }
    }
})