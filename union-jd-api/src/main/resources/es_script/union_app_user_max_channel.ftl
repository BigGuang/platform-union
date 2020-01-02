{
"from": "0",
"size": "1",
"_source" : {
"includes" : [ "channel_name","user_name" ]
},
"query": {
"bool": {
"filter": [
{
"prefix": {
"channel_name": "W10"
}
}
]
}
},
"sort": [
{
"channel_name": {
"order": "desc"
}
}
]
}