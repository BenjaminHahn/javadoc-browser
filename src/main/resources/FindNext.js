function () {
            var str = document.getElementById ("findInput").value;
            if (str == "") {
                alert ("Please enter some text to search!");
                return;
            }

            if (window.find) {        // Firefox, Google Chrome, Safari
                var found = window.find (str);
                if (!found) {
                    alert ("The following text was not found:\n" + str);
                }
            }
            else {
                alert ("Your browser does not support this example!");
            }
        }