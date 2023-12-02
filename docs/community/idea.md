# concept

### Is smart-doc suitable for design-first development?
Some old-school programmers or so-called architects with many years of experience feel that `smart-doc`, a code-based scanning tool, is of no use to the design-first development model.
is it really like this? Let's take a look.
### Design first model
- Design documents and interface protocols written by more experienced people.
- After the design is completed, the business development engineer will develop the business logic based on the design document.
- Architects can also directly define the interface framework of the program like some foreign architects, and then deliver it to business engineers to fill in business code.

Design first is usually found in mature development teams. In the code-first mode, the interface protocols have been defined during the design stage. But `smart-doc` is still useful.
- The omission of continuous updates in the later stages of well-designed interface protocols is still a problem. Human nature is lazy. `smart-doc` can maintain the consistency of documentation and code.
- When a new member joins the team to look at the code, it is easier to track the document written in `html` than in `word`. New members can get started directly by opening the `debug` document page to debug and become familiar with the business.
- `smart-doc` uses source code analysis and has higher code standard requirements than other tools. Using this tool can directly unify the team style.
- You can also customize development based on `smart-doc` and transfer documents to similar interface document management such as `yapi`.

> At present, the mainstream design-first document methods in China are mainly word or markdown. Word's page turning is very unfriendly to interface display.

### Code first
In this code-first mode, you can use `smart-doc` to write code and export interface documents at the same time. Then use `smart-doc` to strictly regulate the code.
It is completely guaranteed that there will not be a big difference in the team's coding style.

> Code first has disadvantages in many teams. Code first, especially for large systems, requires high ability of code writers.
The ability to combine code and business needs to be very strong and be able to take expansion and business boundaries into consideration. You can run the code first, or you can take another route.
After the team's architect directly builds the project framework, he defines the interface framework code and fills in all the empty business logic parts. With the interface, smart-doc can already scan and generate interface documents.

### Summarize
In short, using a documentation tool has nothing to do with which model your team adopts. Tools are designed to help the team complete their work better or improve efficiency in some aspects.
As technicians, we must also look forward. There will always be new technologies, new frameworks, and new tools to solve some of the problems of the past. Being curious about new things is also an attitude towards life.

### Can I participate in open source as a newbie?
The most important indicator of open source software is not technology. The activity of the community and the number of code contributors are the indicators to measure the sustainable development of open source software.
Therefore, as long as you have time and are willing to contribute, the author of `smart-doc` will basically guide you on how to modify the `issue`.

Among the core maintainers of `smart-doc`, there are also students who started to join the development as students and later successfully obtained an `offer` from a major domestic manufacturer.
Therefore, we very much welcome students who are willing to participate in open source to join. Even if you are a novice, don’t worry.
`smart-doc` has been adopted by many domestic first- and second-tier manufacturers, and there will only be more in the future. Participating in open source is of great help to both rookies and newcomers.