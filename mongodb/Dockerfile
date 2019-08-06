FROM mongo:4.0.10
MAINTAINER Alexander Lukyanchikov <sqshq@sqshq.com>

ENV AUTH yes
ENV STORAGE_ENGINE wiredTiger
ENV JOURNALING yes

ADD run.sh /run.sh
ADD ./dump /
ADD set_account.sh /set_account.sh

RUN \
 chmod +x /run.sh && \
 chmod +x /set_account.sh && \
 apt-get update && apt-get dist-upgrade -y --force-yes && \
 apt-get install psmisc -y -q && \
 apt-get autoremove -y && apt-get clean && \
 rm -rf /var/cache/* && rm -rf /var/lib/apt/lists/*

CMD ["/run.sh"]
