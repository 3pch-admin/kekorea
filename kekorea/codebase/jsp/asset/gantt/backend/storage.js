const striptags = require("striptags");

class Storage {
    constructor(data) {
        this._data = data;
    }
    all() {
        return this._data;
    }
    get(id, table) {
        return this._data[table].find(function(entry) {
            return id === entry.id;
        });
    }
    delete(id, table) {
        this._data[table] = this._data[table].filter(function(entry) {
            return id !== entry.id;
        });
    }
    update(id, table, newData) {
        return this._data[table].find(function(entry, index, arr) {
            if (id == entry.id) {
                arr[index] = _addId(id, sanitizeObject(newData));
                return true;
            }
        });
    }
    insert(table, data) {
        data.id = uid();
        this._data[table].push(sanitizeObject(data));
        return data;
    }
}

var seed = Date.now();
function uid(){
    return seed++;
}

function sanitizeObject(data){
    for(var i in data){
        if(typeof data[i] === "string"){
            data[i] = striptags(data[i]);
        }else if(typeof data[i] === "object"){
            sanitizeObject(data[i]);
        }
    }
    return data;
}

function _addId(id, data) {
    return Object.assign(data, {id: id});
}

module.exports = Storage;