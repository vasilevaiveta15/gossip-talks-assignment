'use strict'
const baseURL = window.location;
//const baseURL = 'http://localhost:8000';
const PAGE_SIZE = 6;

$('.toast').toast();
$('#registerForm').submit(registerUser);
$('#passwordForm').submit(changePassword);
$('#postGossip').submit(postGossip);
$('#menu-toggle').click(function(e) {
  e.preventDefault();
  $(this).parent().toggleClass('active');
  $("#wrapper").toggleClass('toggled');
});
var pageSelection = $('#page-selection').bootpag({
  total: 10,
  page: 1,
  maxVisible: PAGE_SIZE
}).on("page", function(event, /* page number here */ num){
  console.log("page number", num);
  getGossips(num);
});

// fetch utility function
function f(path, body=null, json=true, queryParams={}) {
  var url = new URL(baseURL);
  url.pathname = path;
  Object.entries(queryParams).forEach((e)=>{
    url.searchParams.append(e[0], e[1]);
  })
  return (body == null ? fetch(url) : fetch(url, { method: 'post', body: body }))
    .then((response) => {
      console.log(response);
      if (response.status === 401) { // not authorized
        $('#registerModal').modal('show');
        throw 'unauthorized';
      }
      if (response.status >= 400) {
        var err = `Error #${response.status}`;
        response.json().then((m) => {
          notify(err, JSON.stringify(m, null, '  '));
        });
        throw err;
      }
      return json ? response.json() : response;
    });
}

// safe escape html code
function htmlDecode(t){
   return t ? $('<div />').html(t).text() : '';
}
// notify utility function
function notify(title, message) {
  message = htmlDecode(message);
  $(`<div class="toast" role="alert"  aria-live="assertive" aria-atomic="true" data-delay="5000">
       <div class="toast-header">
         <img src="images/favicon.png" width="32px" class="rounded mr-2" alt="favicon">
         <strong class="mr-auto">${title}</strong>
       </div>
       <div class="toast-body" style="white-space: pre">${message}</div>
     </div>`)
     .toast()
     .toast('show')
     .on('hidden.bs.toast', function() {
        $(this).toast('dispose').remove();
     })
     .appendTo('#notifications');
}

function getCurrentUser() {
  f('/api/v1/users/me')
    .then((data) => {
      $('#currentUser').text(data.username);
    });
}

function getUsers() {
  f('/api/v1/users')
    .then((data) => {
      var usersFriends = $('#usersFriends').empty();
      var usersOthers = $('#usersOthers').empty();
      data.forEach(user => {
        var where = user.following ? usersFriends : usersOthers
        where.append(
          `<a
            href="javascript:userFollow('${user.username}', ${!user.following})"
            class="list-group-item list-group-item-action bg-light">
            <i class="fa fa-${user.following?'minus':'plus'}-circle"></i>
            ${user.username}
          </a>`);
      });
    });
  return false;
}

function changePassword() {
  f('/api/v1/users/me', new FormData(this), false)
    .then((response) => {
      if (response.status != 200) {
        $('#changePasswordModal .alert').show();
      } else {
        $('#changePasswordModal').modal('hide');
      }
    });
  return false;
}

function userFollow(username, follow) {
  var form = new FormData();
  form.append('follow', follow);
  f(`/api/v1/users/${username}/follow`, form, false)
    .then(getUsers);
  return false;
}

function getGossips(page=1) {
  f('/api/v1/gossips', null, true, {pageSize: PAGE_SIZE, pageNo: page - 1})
    .then((data) => {
      // update pagination
      pageSelection.bootpag({
        total: Math.ceil(data.total/data.pageSize),
        page: page,
        maxVisible: PAGE_SIZE
      });
      // update posts
      var posts = $('#posts').empty();
      data.content.forEach(gossip => {
        posts.append(`
          <a href="#" class="list-group-item list-group-item-action">
            <div class="d-flex w-100 justify-content-between">
              <h5>${gossip.username} <small class="text-muted">#${gossip.id}</small></h5>
              <small>${moment(gossip.datetime).fromNow()}</small>
            </div>
            <p class="mb-1">${gossip.text}</p>
          </a>
        `);
      });
    });
}

function postGossip() {
  f('/api/v1/gossips', new FormData(this), false)
    .then((data) => {
      notify('Gossip Posted!', this['text'].value);
    });
  return false;
}

function registerUser() {
  console.log('registerUser');
  f('/api/v1/users', new FormData(this), false)
    .then((data) => {
      if (data.status === 200) {
        window.location.reload();
      } else {
        var list = document.getElementsByClassName("alert-warning")[0];
        list.style.display = "block";
      }
    });
  return false;
}

function loadContent() {
  getUsers();
  getCurrentUser();
  getGossips();
}

loadContent();
