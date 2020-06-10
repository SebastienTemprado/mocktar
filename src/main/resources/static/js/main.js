function addMock() {
    alert("mock added!");
}

function removeMock() {
    alert("mock removed!");
}

var app = new Vue({
    el: '#app',
    data: {
        mocks: [],
        message: '',
        name: ''
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
        getMock: function(event) {
            const vm = this;
            axios.get(`http://localhost:8080/mocks?name=${vm.name}`)
            .then(function (response) {
                if (response.data.length == 0) {
                    vm.message = 'No result';
                } else {
                    vm.message = response.data[0].id;
                }
            })
            .catch(function (error) {
                vm.message = 'Error! Could not reach the API. ' + error;
            })
            
        }
    }
})