describe('template spec', () => {
  it('passes', () => {
    cy.visit('http://localhost:3000/');
    cy.get('[placeholder="Email address"]').type('bogdanadrian.ciupe@gmail.com');
    cy.get('[placeholder="Password"]').type('123456');
    cy.get('.login-button').click();
    cy.get(':nth-child(1) > .add-friend-btn').click();
  })
})