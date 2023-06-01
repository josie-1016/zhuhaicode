FROM hyperledger/fabric-ccenv:1.4

RUN buildDeps='flex bison lzip' \
    && apt-get update \
        && apt-get install -y $buildDeps \
        && mkdir temp \
        && cd temp \
        && wget https://gmplib.org/download/gmp/gmp-6.1.2.tar.lz \
        && lzip -d gmp-6.1.2.tar.lz \
        && tar -xvf gmp-6.1.2.tar \
        && cd gmp-6.1.2 \
        && ./configure \
        && make \
	&& make check \
        && make install  \
        && cd .. \
        && wget https://crypto.stanford.edu/pbc/files/pbc-0.5.14.tar.gz \
        && tar -xvf pbc-0.5.14.tar.gz \
        && cd pbc-0.5.14 \
        && ./configure \
        && make \
        && make install \
        && cd .. \
        && rm -rf temp/ \
        && apt-get purge -y --auto-remove $buildDeps
ENV PATH $PATH:/usr/local/lib

