function addMock() {
    alert("mock added!");
}

function removeMock() {
    alert("mock removed!");
}

var app = new Vue({
    el: '#app',
    data: {
        message: 'Hello Vue!',
        name: ''
    },
    created: function() {
        const vm = this;
        axios.get('https://yesno.wtf/api')
        .then(function (response) {
            vm.message = response.data.answer;
        })
        .catch(function (error) {
            vm.message = 'Error! Could not reach the API. ' + error;
        })
    },
    methods: {
        getMock: function(event) {
            const vm = this;
            axios.get(`http://localhost:8080/mock?name=${vm.name}`)
            .then(function (response) {
                vm.message = response.data.id;
            })
            .catch(function (error) {
                vm.message = 'Error! Could not reach the API. ' + error;
            })
            
        }
    }
})