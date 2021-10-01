hello_world() {
    echo 'hello, world';
    for f in *.bmp
        do
            echo ${f:0:9} images/${f} 0 0 0 0
        done
}

hello_world
