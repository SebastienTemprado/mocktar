function addMock() {
    alert("mock added!");
}

function removeMock() {
    alert("mock removed!");
}

var app = new Vue({
    el: '#app',
    data: {
        message: 'Hello Vue!'
    },
    created: function() {
        var vm = this;
        axios.get('https://yesno.wtf/api')
        .then(function (response) {
            vm.message = response.data.answer;
        })
        .catch(function (error) {
            vm.message = 'Error! Could not reach the API. ' + error;
        })
    }
})